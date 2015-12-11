import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;

import javax.swing.*;

/**
 * Created by Matt on 25/11/2015.
 */
public class RsyncConfiguration implements Configurable {
    Project _project;

    private JPanel panel;
    private JTextField txtCommand;
    private JTextField txtReset;
    private JCheckBox enabledCheckBox;

    public RsyncConfiguration(final Project currentProject){
        _project = currentProject;
    }

    public JComponent createComponent(){
        return panel;
    }
    public void apply(){
        RsyncService service = RsyncService.getInstance(_project);
        service.command = txtCommand.getText();
        service.reset = txtReset.getText();
        service.enabled = enabledCheckBox.isSelected();
    }
    public void reset() {
        RsyncService service = RsyncService.getInstance(_project);
        if(txtCommand != null){
            if(service.command != null){
                txtCommand.setText(service.command);
            } else {
                txtCommand.setText("");
            }
        }
        if(txtReset != null){
            if(service.reset != null){
                txtReset.setText(service.reset);
            } else {
                txtReset.setText("");
            }
        }
    }
    public void disposeUIResources(){

    }
    public boolean isModified() {
        RsyncService service = RsyncService.getInstance(_project);

        boolean modified = false;

        if(txtCommand != null){
            if(service.command != null){
                if(!txtCommand.getText().equals(service.command)){
                    modified = true;
                }
            } else {
                if(!txtCommand.getText().equals("")){
                    modified = true;
                }
            }
        }
        if(txtReset != null){
            if(service.reset != null){
                if(!txtReset.getText().equals(service.reset)){
                    modified = true;
                }
            } else {
                if(!txtReset.getText().equals("")){
                    modified = true;
                }
            }
        }
        if(enabledCheckBox != null){
            if(service.enabled != null){
                if(service.enabled != enabledCheckBox.isSelected()){
                    modified = true;
                }
            } else {
                if(enabledCheckBox.isSelected()){
                    modified = true;
                }
            }
        }

        return modified;
    }
    public String getHelpTopic(){
        return "";
    }
    public String getDisplayName(){
        return "STC Rsync";
    }

    private void createUIComponents() {
        RsyncService service = RsyncService.getInstance(_project);

        String command = (service.command != null ? service.command : "");
        txtCommand = new JTextField(command);

        String reset = (service.reset != null ? service.reset : "");
        txtReset = new JTextField(reset);

        enabledCheckBox = new JCheckBox();
        if(service.enabled != null){
            enabledCheckBox.setSelected(service.enabled);
        } else {
            if(command.length() > 0 || reset.length() > 0){
                enabledCheckBox.setSelected(true);
            }
        }
    }
}
