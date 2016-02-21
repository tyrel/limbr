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

package management.limbr.ui.users;

import com.vaadin.data.util.BeanItemContainer;
import management.limbr.data.UserRepository;
import management.limbr.data.model.User;
import management.limbr.ui.Presenter;
import management.limbr.ui.usereditor.UserEditorPresenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Set;

@Presenter
public class UsersPresenter implements UsersView.UsersViewListener, UserEditorPresenter.UserChangeHandler, Serializable {

    @Autowired
    private UserRepository repository;

    @Autowired
    private UserEditorPresenter editorPresenter;

    private UsersView view;

    @PostConstruct
    public void init() {
        editorPresenter.setUserChangeHandler(this);
    }

    @Override
    public void viewInitialized(UsersView view) {
        this.view = view;
    }

    @Override
    public BeanItemContainer<User> listUsers(String filter) {
        if (StringUtils.isEmpty(filter)) {
            return new BeanItemContainer<>(User.class, repository.findAll());
        } else {
            return new BeanItemContainer<>(User.class,
                    repository.findByUsernameStartsWithIgnoreCase(filter));
        }
    }

    @Override
    public void usersSelected(Set<Object> users) {
        if (users.isEmpty()) {
            editorPresenter.hide();
        } else {
            editorPresenter.editUser((User)users.iterator().next());
        }
    }

    @Override
    public void addNewClicked() {
        editorPresenter.editUser(new User("", "", "", ""));
    }

    @Override
    public void onUserChanged() {
        editorPresenter.hide();
        view.refresh();
    }
}
