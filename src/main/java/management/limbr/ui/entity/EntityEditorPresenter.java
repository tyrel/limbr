/*
 * Copyright (c) 2016 Tyrel Haveman and contributors.
 *
 * This file is part of Limbr.
 *
 * Limbr is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Limbr is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Limbr.  If not, see <http://www.gnu.org/licenses/>.
 */

package management.limbr.ui.entity;

import management.limbr.data.model.BaseEntity;
import management.limbr.data.model.Password;
import management.limbr.data.model.util.EntityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.lang.reflect.Field;

public abstract class EntityEditorPresenter<T extends BaseEntity> implements EntityEditorView.Listener, Serializable, ApplicationContextAware {
    public static final String DEFAULT_VALUE = " | | | | | ";
    private transient JpaRepository<T, Long> repository;
    private T entity;
    private transient EntityEditorView<T> view;
    private transient EntityChangeHandler entityChangeHandler;
    private transient ApplicationContext applicationContext;
    private transient EntityUtil entityUtil;

    private static final Logger LOG = LoggerFactory.getLogger(EntityEditorPresenter.class);

    @Autowired
    public EntityEditorPresenter(JpaRepository<T, Long> repository, EntityUtil entityUtil) {
        this.repository = repository;
        this.entityUtil = entityUtil;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    protected abstract Class<? extends EntityEditorView<T>> getViewBeanClass();

    public void hide() {
        getView().hide();
    }

    public void edit(T entity) {
        final boolean persisted = entity.getId() != null;

        if (persisted) {
            this.entity = repository.findOne(entity.getId());
        } else {
            this.entity = entity;
        }

        getView().setDeleteVisible(persisted);

        for (java.lang.reflect.Field field : entity.getClass().getDeclaredFields()) {
            if ("id".equals(field.getName()) || field.getName().contains("$")) {
                continue;
            }
            Object value;

            Password passwordAnnotation = field.getAnnotation(Password.class);
            if (passwordAnnotation != null) {
                value = DEFAULT_VALUE;
            } else {
                value = entityUtil.callGetter(entity, field.getName());
            }
            if (value == null && field.getType().equals(String.class)) {
                value = "";
            }
            getView().setFieldValue(field.getName(), value);
        }

        getView().show();
    }

    @Override
    public void save() {
        for (java.lang.reflect.Field field : entity.getClass().getDeclaredFields()) {
            if ("id".equals(field.getName()) || field.getName().contains("$")) {
                continue;
            }
            commitFieldValue(field);
        }

        repository.save(entity);

        if (entityChangeHandler != null) {
            entityChangeHandler.onEntityChanged();
        }

        getView().hide();
    }

    private void commitFieldValue(Field field) {
        Password passwordAnnotation = field.getAnnotation(Password.class);
        if (passwordAnnotation != null) {
            String value = getView().getFieldValue(String.class, field.getName());
            if (!DEFAULT_VALUE.equals(value)) {
                try {
                    Field saltField = entity.getClass().getDeclaredField(passwordAnnotation.saltWith());

                    Object saltValue = getView().getFieldValue(saltField.getType(), saltField.getName());

                    entityUtil.callSetter(entity, field.getName(), entityUtil.generatePasswordHash(saltValue.toString(), value));

                } catch (NoSuchFieldException ex) {
                    LOG.warn("Cannot find field " + passwordAnnotation.saltWith() + " to hash with password field "
                            + field.getName() + " of " + entity.getClass().getName(), ex);
                }
            }
        } else {
            entityUtil.callSetter(entity, field.getName(), getView().getFieldValue(field.getType(), field.getName()));
        }
    }

    @Override
    public void deleteClicked() {
        getView().confirmDelete(entity);
    }

    @Override
    public void deleteConfirmed() {
        repository.delete(entity);

        if (entityChangeHandler != null) {
            entityChangeHandler.onEntityChanged();
        }

        getView().hide();
    }

    @Override
    public void cancel() {
        getView().hide();
    }

    @FunctionalInterface
    public interface EntityChangeHandler {
        void onEntityChanged();
    }

    public void setEntityChangeHandler(EntityChangeHandler entityChangeHandler) {
        this.entityChangeHandler = entityChangeHandler;
    }

    private EntityEditorView<T> getView() {
        if (view == null) {
            view = applicationContext.getBean(getViewBeanClass());
        }

        return view;
    }

}
