package dela.ui

import com.vaadin.data.Item
import com.vaadin.data.util.BeanItem
import com.vaadin.ui.ComboBox
import com.vaadin.ui.Component
import com.vaadin.ui.Field
import com.vaadin.ui.Form
import com.vaadin.ui.FormFieldFactory
import com.vaadin.ui.TwinColSelect
import com.vaadin.ui.Window
import dela.State
import dela.Subject
import dela.ui.common.EntityForm

/**
 * @author vedi
 * date 07.07.2010
 * time 22:38:13
 */
class SetupWindow extends Window implements FormFieldFactory {

    def storeService
    def setup

    private Form form

    def SetupWindow() {

        storeService = getBean(StoreService.class)

        this.caption = i18n("entity.setup.caption", "setup")

        form = new EntityForm(editable:true)

        setup = storeService.setup

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
    }

    def void attach() {
        super.attach();
        
        form.layout.components[0].focus()
    }

    def saveSetup = {item ->
        Setup.withTransaction {
            storeService.setup = setup
        }
    }

    Field createField(Item item, Object propertyId, Component component) {
        String caption = i18n("entity.setup.field.${propertyId}.label", propertyId)

        if ('activeSubject'.equals(propertyId)) {
            def comboBox = new ComboBox(caption: caption, immediate: true)
            Subject.withTransaction {
                Subject.findAllByOwnerOrIsPublic(storeService.account, true).each {
                    comboBox.addItem it
                }
            }

            comboBox
        } else if ('filterSubjects'.equals(propertyId)) {
            def twinColSelect = new TwinColSelect(caption: caption, immediate: true)
            Subject.withTransaction {
                Subject.findAllByOwnerOrIsPublic(storeService.account, true).each {
                    twinColSelect.addItem it
                }
            }

            twinColSelect
        } else if ('filterStates'.equals(propertyId)) {
            def twinColSelect = new TwinColSelect(caption: caption, immediate: true)
            State.withTransaction {
                State.findAll().each {
                    twinColSelect.addItem it
                }
            }
            twinColSelect
        }
    }
}
