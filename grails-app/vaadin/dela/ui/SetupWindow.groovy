package dela.ui

import com.vaadin.data.Item
import com.vaadin.data.util.BeanItem
import com.vaadin.ui.ComboBox
import com.vaadin.ui.Component
import com.vaadin.ui.Field
import com.vaadin.ui.Form
import com.vaadin.ui.FormFieldFactory
import com.vaadin.ui.OptionGroup
import com.vaadin.ui.Window
import dela.Setup
import dela.State

import dela.Subject
import dela.ui.common.EntityForm

/**
 * @author vedi
 * date 07.07.2010
 * time 22:38:13
 */
class SetupWindow extends Window implements FormFieldFactory {

    Setup setup
    def sessionContext
    def accountService

    private Form form

    def void attach() {

        super.attach();
        
        this.caption = i18n("entity.setup.caption", "setup")

        form = new EntityForm(editable:true)

        setup = sessionContext.setup

        Setup.withTransaction {
            def setupItem = new BeanItem(setup)

            form.formFieldFactory = this
            form.itemDataSource = setupItem
            form.visibleItemProperties = ['activeSubject', 'filterStates', 'filterSubjects']
        }
        form.saveHandler = saveSetup

        this.addComponent(form)

        this.layout.setSizeUndefined()
        this.center()

        form.layout.components[0].focus()
    }

    def saveSetup = {item ->
        accountService.saveSetup(sessionContext, setup)
    }

    Field createField(Item item, Object propertyId, Component component) {
        String caption = i18n("entity.setup.field.${propertyId}.label", propertyId)

        if ('activeSubject'.equals(propertyId)) {
            def comboBox = new ComboBox(caption: caption, immediate: true)
            Subject.withTransaction {
                Subject.findAllByOwnerOrIsPublic(sessionContext.account, true).each {
                    comboBox.addItem it
                }
            }

            comboBox
        } else if ('filterSubjects'.equals(propertyId)) {
            def select = new OptionGroup(caption: caption, immediate: true, multiSelect:true)
            Subject.withTransaction {
                Subject.findAllByOwnerOrIsPublic(sessionContext.account, true).each {
                    select.addItem it
                }
            }

            select
        } else if ('filterStates'.equals(propertyId)) {
            def select = new OptionGroup(caption: caption, immediate: true, multiSelect:true)
            State.withTransaction {
                State.findAll().each {
                    select.addItem it
                }
            }
            select
        }
    }
}
