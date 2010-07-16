package dela;

import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.Button;
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

    no = new Button(noLabel, this);
    no.setClickShortcut(ShortcutAction.KeyCode.ESCAPE);


    setModal(true);

    this.callback = callback;

    if (question != null) {
      addComponent(new Label(question));
    }

    HorizontalLayout hl = new HorizontalLayout();
    hl.addComponent(yes);
    hl.addComponent(no);
    addComponent(hl);
  }

  public YesNoDialog(String caption, String question, Callback callback) {
    this(caption, question, "yes", "no", callback);
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