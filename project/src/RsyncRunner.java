import com.intellij.execution.ExecutionException;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessListener;
import com.intellij.execution.process.ScriptRunnerUtil;
import com.intellij.ide.actions.SaveAllAction;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Created by Matt on 25/11/2015.
 */
public class RsyncRunner extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project project = getEventProject(e);

        ApplicationManager.getApplication().saveAll();
        RsyncService svc = RsyncService.getInstance(project);

        boolean enabled;
        if(svc.enabled != null){
            enabled = svc.enabled;
        } else {
            enabled = (svc.command != null && svc.command.length() > 0);
        }

        if(enabled){
            StatusBar statusBar = WindowManager.getInstance().getStatusBar(PlatformDataKeys.PROJECT.getData(e.getDataContext()));
            ProgressManager.getInstance().run(new Task.Backgroundable(project, "Syncing Files"){
                @Override
                public void run(@NotNull ProgressIndicator progressIndicator) {
                    progressIndicator.setFraction(0.0);
                    if(svc.command != null && svc.command.length() > 0){
                        try{
                            String command;
                            String[] args;

                            int space = svc.command.indexOf(" ");
                            if(space > -1){
                                command = svc.command.substring(0, space);

                                args = svc.command.substring(space + 1).split(" ");
                            } else {
                                command = svc.command;
                                args = new String[0];
                            }
                            progressIndicator.setFraction(0.5);
                            OSProcessHandler handle = ScriptRunnerUtil.execute(command, null, null, args);
                            handle.addProcessListener(new ProcessListener() {
                                @Override
                                public void startNotified(ProcessEvent event) {

                                }

                                @Override
                                public void processTerminated(ProcessEvent event) {
                                    progressIndicator.setFraction(1.0);
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
                        } catch(ExecutionException error){
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    JBPopupFactory.getInstance().createHtmlTextBalloonBuilder(error.getMessage(), MessageType.ERROR, null)
                                            .setFadeoutTime(3000)
                                            .createBalloon()
                                            .show(RelativePoint.getCenterOf(statusBar.getComponent()), Balloon.Position.above);
                                }
                            });
                        }
                    } else {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                JBPopupFactory.getInstance().createHtmlTextBalloonBuilder("Rsync command is not configured", MessageType.ERROR, null)
                                        .setFadeoutTime(3000)
                                        .createBalloon()
                                        .show(RelativePoint.getCenterOf(statusBar.getComponent()), Balloon.Position.above);
                            }
                        });
                    }
                }
            });
        }
    }
}
