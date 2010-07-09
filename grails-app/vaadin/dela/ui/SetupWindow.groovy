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
import dela.Setup
import dela.State
import dela.StoreService
import dela.Subject
import dela.ui.common.EntityForm

/**
 * @author vedi
 * date 07.07.2010
 * time 22:38:13
 */
class SetupWindow extends Window implements FormFieldFactory {

    def SetupWindow() {

        this.caption = 'Setup'

        Form form = new EntityForm()

        Setup.withTransaction {
            def setupItem = loadSetup()

            form.formFieldFactory = this
            form.itemDataSource = setupItem
            form.visibleItemProperties = ['activeSubject', 'filterStates', 'filterSubjects']
        }
        form.saveHandler = saveSetup

        this.addComponent(form)

        this.layout.setSizeUndefined()
        this.center()
    }

    def loadSetup() {
        Setup.withTransaction {
            Setup setup = Setup.count() ? Setup.findAll()[0] : new Setup()
            new BeanItem(setup)
        }
    }

    def saveSetup = {item ->
        Setup.withTransaction {
            Long id = item.getItemProperty("id")?.value as Long
            def setup
            if (id) {
                setup = Setup.get(id)
                assert setup
            } else {
                setup = new Setup()
            }
            setup.activeSubject = item.getItemProperty('activeSubject').value

            def oldSubjects = new HashSet(setup.filterSubjects)
            oldSubjects.each() {
                setup.removeFromFilterSubjects(it) // it.merge()???
            }
            item.getItemProperty('filterSubjects').value.each {
                setup.addToFilterSubjects(it.merge())
            }

            def oldStates = new HashSet(setup.filterStates)
            oldStates.each() {
                setup.removeFromFilterStates(it) // it.merge()???
            }
            item.getItemProperty('filterStates').value.each {
                setup.addToFilterStates(it.merge())
            }

            if (setup.save()) {
                def storeService = getBean(StoreService.class)
                storeService.setup = setup
            } else {
                setup.errors.each {
                    println it
                }
            }
        }
    }

    Field createField(Item item, Object propertyId, Component component) {
        if ('activeSubject'.equals(propertyId)) {
            def comboBox = new ComboBox(caption:'activeSubject', immediate: true)
            Subject.withTransaction {
                Subject.findAll().each {
                    comboBox.addItem it
                }
            }

            comboBox
        } else if ('filterSubjects'.equals(propertyId)) {
            def twinColSelect = new TwinColSelect(caption:'filterSubjects', immediate: true)
            Subject.withTransaction {
                Subject.findAll().each {
                    twinColSelect.addItem it
                }
            }

            twinColSelect
        } else if ('filterStates'.equals(propertyId)) {
            def twinColSelect = new TwinColSelect(caption:'filterStates', immediate: true)
            State.withTransaction {
                State.findAll().each {
                    twinColSelect.addItem it
                }
            }
            twinColSelect
        }
    }
}
