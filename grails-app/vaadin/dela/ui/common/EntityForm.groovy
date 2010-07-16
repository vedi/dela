package dela.ui.common

import com.vaadin.event.ShortcutAction.KeyCode
import com.vaadin.event.ShortcutAction.ModifierKey
import com.vaadin.ui.Button
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.Button.ClickListener
import com.vaadin.ui.Form

/**
 * @author vedi
 * date 02.07.2010
 * time 23:15:53
 */
class EntityForm extends Form implements Button.ClickListener {

    def saveHandler

    Button commitButton
    Button discardButton

    def EntityForm() {
        this.writeThrough =  false
        this.invalidCommitted = false
    }

    def void attach() {

        initButtons()

        super.attach();
    }

    protected void initButtons() {
        commitButton = new Button()
        commitButton.caption = i18n('button.ok.label', 'ok')
        commitButton.addListener(this as ClickListener)
        commitButton.setClickShortcut(KeyCode.ENTER, ModifierKey.CTRL)
        footer.addComponent(commitButton)

        discardButton = new Button()
        discardButton.caption = i18n('button.cancel.label', 'cancel')
        discardButton.addListener(this as ClickListener)
        discardButton.setClickShortcut(KeyCode.ESCAPE)
        footer.addComponent(discardButton)
    }


    void buttonClick(ClickEvent clickEvent) {
        if (clickEvent.button == commitButton) {
            commit()
            saveHandler(getItemDataSource())
        } else {
            discard()
        }

        def mainWindow = window.application.mainWindow
        
        // TODO: Remove dependency
        mainWindow.removeWindow window
        mainWindow.application.table.addButton.focus()
        //\\
    }
}
