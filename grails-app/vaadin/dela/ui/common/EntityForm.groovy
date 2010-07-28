package dela.ui.common

import com.vaadin.event.ShortcutAction.KeyCode
import com.vaadin.event.ShortcutAction.ModifierKey
import com.vaadin.ui.Button
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.Button.ClickListener
import com.vaadin.ui.ComponentContainer
import com.vaadin.ui.Form

/**
 * @author vedi
 * date 02.07.2010
 * time 23:15:53
 */
class EntityForm extends Form implements Button.ClickListener {

    def saveHandler

    boolean editable = false

    Button okButton
    Button cancelButton

    def EntityForm() {
        this.writeThrough =  false
        this.invalidCommitted = false
    }

    def void attach() {
        
        initButtons(footer)

        super.attach();
    }

    protected void initButtons(ComponentContainer componentContainer) {
        if (editable) {
            okButton = new Button()
            okButton.caption = i18n('button.ok.label', 'ok')
            okButton.addListener(this as ClickListener)
            okButton.setClickShortcut(KeyCode.ENTER, ModifierKey.CTRL)
            componentContainer.addComponent(okButton)
        }

        cancelButton = new Button()
        cancelButton.caption = editable ? i18n('button.cancel.label', 'cancel') : i18n('button.close.label', 'close')
        cancelButton.addListener(this as ClickListener)
        cancelButton.setClickShortcut(KeyCode.ESCAPE)
        componentContainer.addComponent(cancelButton)
    }


    void buttonClick(ClickEvent clickEvent) {
        if (clickEvent.button == okButton) {
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
