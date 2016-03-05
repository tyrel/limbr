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

package management.limbr.ui.projects;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.UI;
import management.limbr.data.ProjectRepository;
import management.limbr.data.model.Project;
import management.limbr.ui.VaadinUI;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

@Test
public class ProjectsPresenterTest {
    private ProjectRepository repository;
    private ProjectsPresenter presenter;

    @BeforeMethod
    public void beforeMethod() {
        VaadinUI ui = mock(VaadinUI.class);
        UI.setCurrent(ui);
        ProjectsView view = mock(ProjectsView.class);
        repository = mock(ProjectRepository.class);

        presenter = new ProjectsPresenter(repository);
        presenter.viewInitialized(view);
    }

    public void listsProjectsEmptyFilter() {
        Project mockProject = mock(Project.class);
        when(repository.findAll()).thenReturn(Collections.singletonList(mockProject));
        BeanItemContainer<Project> result = presenter.listEntities("");
        assertEquals(result.size(), 1);
        assertEquals(result.getIdByIndex(0), mockProject);
    }

    public void listsProjectsWithFilter() {
        Project mockProject = mock(Project.class);
        when(repository.findByNameStartsWithIgnoreCase("filter")).thenReturn(Collections.singletonList(mockProject));
        BeanItemContainer<Project> result = presenter.listEntities("filter");
        assertEquals(result.size(), 1);
        assertEquals(result.getIdByIndex(0), mockProject);
    }
}
