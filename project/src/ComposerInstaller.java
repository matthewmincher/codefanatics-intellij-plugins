import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.PathEnvironmentVariableUtil;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessListener;
import com.intellij.execution.process.ScriptRunnerUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;


public class ComposerInstaller extends AnAction{
    private int counter;

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project project = getEventProject(e);
        RsyncService svc = RsyncService.getInstance(project);

        if(svc.composer != null){
            StatusBar statusBar = WindowManager.getInstance().getStatusBar(PlatformDataKeys.PROJECT.getData(e.getDataContext()));
            ProgressManager.getInstance().run(new Task.Backgroundable(project, "Installing Dependencies"){
                @Override
                public void run(@NotNull ProgressIndicator progressIndicator) {
                    progressIndicator.setFraction(0.0);

                    String[] paths = svc.composer.split(",");
                    int path_count = paths.length;
                    counter = 0;

                    for(String s: paths){
                        try{
                            String composer_alias;
                            if(SystemInfo.isWindows){
                                composer_alias = "composer.bat";
                            } else {
                                composer_alias = "composer";
                            }

                            File composer_exec = PathEnvironmentVariableUtil.findInPath(composer_alias);
                            if(composer_exec != null){
                                OSProcessHandler handle = ScriptRunnerUtil.execute(composer_exec.getAbsolutePath(), null, null, new String[]{"install", "--working-dir="+s});
                                handle.addProcessListener(new ProcessListener() {
                                    @Override
                                    public void startNotified(ProcessEvent event) {

                                    }

                                    @Override
                                    public void processTerminated(ProcessEvent event) {
                                        counter++;
                                    }

                                    @Override
                                    public void processWillTerminate(ProcessEvent event, boolean willBeDestroyed) {

                                    }

                                    @Override
                                    public void onTextAvailable(ProcessEvent event, Key outputType) {

                                    }
                                });
                                handle.startNotify();
                                handle.waitFor();

                                progressIndicator.setFraction(counter / path_count);
                            }
                        } catch(Exception e){
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    JBPopupFactory.getInstance().createHtmlTextBalloonBuilder(e.getMessage(), MessageType.ERROR, null)
                                            .setFadeoutTime(3000)
                                            .createBalloon()
                                            .show(RelativePoint.getCenterOf(statusBar.getComponent()), Balloon.Position.above);
                                }
                            });
                        }
                    }

                }
            });
        }
    }
}
