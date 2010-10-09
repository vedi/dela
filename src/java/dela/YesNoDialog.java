package dela;

import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

/**
 * @author vedi
 *         date 04.07.2010
 *         time 1:46:56
 */
public class YesNoDialog extends Window implements Button.ClickListener {

    Callback callback;
    Button yes;
    Button no;

    public YesNoDialog(String caption, String question, String yesLabel, String noLabel, Callback callback) {
        super(caption);

        yes = new Button(yesLabel, this);
        yes.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        yes.setWidth("50px");

        no = new Button(noLabel, this);
        no.setClickShortcut(ShortcutAction.KeyCode.ESCAPE);
        no.setWidth("50px");


        setModal(true);

        this.callback = callback;

        if (question != null) {
            addComponent(new Label(question));
        }

        GridLayout gridLayout = new GridLayout(3, 1);
        gridLayout.addComponent(yes, 1, 0);
        gridLayout.addComponent(no, 2, 0);
        gridLayout.setColumnExpandRatio(0, 1);
        gridLayout.setWidth("100%");
        addComponent(gridLayout);
    }

    @Override
    public void attach() {
        super.attach();
        no.focus();
    }

    public void buttonClick(Button.ClickEvent event) {
        if (getParent() != null) {
            ((Window) getParent()).removeWindow(this);
        }
        callback.onDialogResult(event.getSource() == yes);
    }

    public interface Callback {

        public void onDialogResult(boolean resultIsYes);
    }

}