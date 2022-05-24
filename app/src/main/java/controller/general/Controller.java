package controller.general;

import view.planning.PlanningController;
import daw.manager.ImportException;
import daw.manager.Manager;

import java.io.File;
import java.util.List;

public interface Controller {

    String SEP = System.getProperty("file.separator");
    String WORKING_DIRECTORY = System.getProperty("user.dir");

    void updateView();

    void saveCurrentProject() throws DownloadingException;

    Manager openProject(File file) throws LoadingException;

    void setPlanningController(PlanningController planningController);

    void newPlanningChannel(String type, String title, String description) throws IllegalArgumentException;

    void newPlanningClip(String type, String title, String description, String channel, Double time, File content) throws IllegalArgumentException, ImportException;

    List<String> getChannelList();

    /**
     * Sets the current project as the template. If this call is successful,
     * then any time a user launches the application, the project that will be opened is the current one.
     * @throws DownloadingException if the writing to file is unsuccessful.
     */
    void setTemplateProject() throws DownloadingException;


    List<String> getClipList(String channel);

    Double getClipTime(String clip);

    Double getClipDuration(String clip);
}
