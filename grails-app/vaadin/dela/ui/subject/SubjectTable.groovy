package dela.ui.subject

import com.vaadin.data.Container
import dela.context.DataContext
import dela.ui.common.EntityForm
import dela.ui.common.EntityTable
import dela.*
import com.vaadin.data.util.BeanItem

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
        return new SubjectForm()
    }

    @Override
    protected def toFormItem(item) {
        assert item
        assert item.bean instanceof Subject

        return new BeanItem(new SubjectCommand(item.bean))
    }

    @Override
    protected fromFormItem(Object item) {
        assert item
        assert item.bean instanceof SubjectCommand

        return new BeanItem(((SubjectCommand)item.bean).getSubject())
    }


}
