import com.intellij.AppTopics;
import com.intellij.ProjectTopics;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.components.*;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManagerAdapter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.messages.impl.Message;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.sun.jna.platform.unix.X11;

import java.util.Map;

/**
 * Created by Matt on 25/11/2015.
 */
@State(name = "RsyncConfiguration",
    storages = {
        @Storage(id = "default", file = StoragePathMacros.PROJECT_FILE)
    }
)
public class RsyncService implements PersistentStateComponent<RsyncService> {

    public String command;
    public String reset;
    public Boolean enabled;

    public RsyncService getState(){
        return this;
    }
    public void loadState(RsyncService state){
        XmlSerializerUtil.copyBean(state, this);
    }

    public static RsyncService getInstance(Project project){
        return ServiceManager.getService(project, RsyncService.class);
    }
}
