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

package management.limbr.ui.roleeditor;

import management.limbr.data.model.Role;
import management.limbr.data.model.util.EntityUtil;
import management.limbr.ui.Presenter;
import management.limbr.ui.entity.EntityEditorPresenter;
import management.limbr.ui.entity.EntityEditorView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

@Presenter
public class RoleEditorPresenter extends EntityEditorPresenter<Role> {

    @Autowired
    public RoleEditorPresenter(JpaRepository<Role, Long> repository, EntityUtil entityUtil) {
        super(repository, entityUtil);
    }

    @Override
    protected Class<? extends EntityEditorView<Role>> getViewBeanClass() {
        return RoleEditorViewImpl.class;
    }
}
