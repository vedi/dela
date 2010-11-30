package dela.ui.subject

import com.vaadin.data.Item
import com.vaadin.ui.Button
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.CheckBox
import com.vaadin.ui.Component
import com.vaadin.ui.ComponentContainer
import com.vaadin.ui.Field
import com.vaadin.ui.FormFieldFactory
import com.vaadin.ui.TextField

import dela.YesNoDialog
import dela.ui.common.EntityForm
import dela.ui.common.EntityTable
import com.vaadin.data.Container
import dela.VaadinService

import dela.SubjectService
import dela.IDataService
import dela.context.DataContext
import dela.TaskService
import dela.MessageService

/**
 * @author vedi
 * date 08.07.2010
 * time 22:33:52
 */
class SubjectTable extends EntityTable {

    def vaadinService
    def messageService

    def gridVisibleColumns = ['name']

    def SubjectTable() {
        this.vaadinService = getBean(VaadinService.class)
        this.messageService = getBean(MessageService.class)
    }

    protected IDataService initDataService() {
        return getBean(SubjectService.class)
    }

    protected Container createContainer(DataContext dataContext) {
        return vaadinService.createSubjectDefaultContainer(dataContext)
    }

    def afterInsert(item) {

        super.afterInsert(item)

        addWindow(new YesNoDialog(
                this.messageService.getSetSubjectActiveConfirmCaption(),
                this.messageService.getSetSubjectActiveConfirmMsg(),
                this.messageService.getYesButtonLabel(),
                this.messageService.getNoButtonLabel(),
                new YesNoDialog.Callback() {
                    public void onDialogResult(boolean yes) {
                        if (yes) {
                            def dataContext = SubjectTable.this.dataContext
                            def subject = getDomain(item)
                            assert subject, getDomain(item).errors()
                            def setup = dataContext.setup
                            setup.setActiveSubject(subject)
                            dataContext.storeService.saveSetup(setup)
                        }
                    }

                }))
    }

    protected EntityForm createForm() {
        return new SubjectForm(dataContext: dataContext)
    }

}
