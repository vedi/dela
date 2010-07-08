package dela

import com.vaadin.ui.Button
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.Form

/**
 * @author vedi
 * date 02.07.2010
 * time 23:15:53
 */
class DomainForm extends Form implements Button.ClickListener {

    def saveHandler

    Button commitButton
    Button discardButton

    def DomainForm() {
        this.writeThrough =  false
        this.invalidCommitted = false
    }

    def void attach() {

        commitButton = new Button("commit", this as Button.ClickListener)
        footer.addComponent(commitButton)

        discardButton = new Button("discard", this as Button.ClickListener)
        footer.addComponent(discardButton)


        super.attach();
    }


    void buttonClick(ClickEvent clickEvent) {
        if (clickEvent.button == commitButton) {
            commit()
            saveHandler(getItemDataSource())
        } else {
            discard()
        }

        window.application.mainWindow.removeWindow window      // TODO: Remove window dependency
    }
}
