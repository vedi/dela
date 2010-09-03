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
import dela.DataService
import dela.Setup
import dela.StoreService
import dela.Subject
import dela.YesNoDialog
import dela.ui.common.EntityForm
import dela.ui.common.EntityTable

/**
 * @author vedi
 * date 08.07.2010
 * time 22:33:52
 */
class SubjectTable extends EntityTable implements FormFieldFactory {

    DataService dataService
    StoreService storeService

    def gridVisibleColumns = ['name']
    def formFieldFactory = this
    def normalizeButton

    def oldSaveHandler
    def newSaveHandler = {item ->
        def id = item.getItemProperty("id")?.value as Long
        boolean isNew = id == null

        oldSaveHandler(item)

        if (isNew) {
            id = item.getItemProperty("id")?.value as Long

            Subject subject = Subject.get(id)
            Setup setup = storeService.setup
            setup.addToFilterSubjects(subject)

            this.window.application.mainWindow.addWindow(new YesNoDialog(
                    i18n('setSubjectActive.confirm.caption', 'setSubjectActive.confirm.caption'),
                    i18n('setSubjectActive.confirm.message', 'setSubjectActive.confirm.message'),
                    i18n('button.yes.label', 'yes'),
                    i18n('button.no.label', 'no'),
                    new YesNoDialog.Callback() {
                        public void onDialogResult(boolean yes) {
                            if (yes) {
                                setup.setActiveSubject(subject)
                                SubjectTable.this.storeService.setup = setup
                            }
                        }

                    }))
        }
    }

    def selector = {startIndex, count, sortProperty, ascendingState ->
        Subject.findAllByOwnerOrIsPublic(storeService.account, true, [offset:startIndex,  max:count, sort:sortProperty, order:ascendingState])
    }

    def counter = {
        Subject.countByOwnerOrIsPublic(storeService.account, true)
    }

    def SubjectTable() {
        oldSaveHandler = saveHandler
        saveHandler = newSaveHandler
        this.dataService = getBean(DataService.class)
        this.storeService = getBean(StoreService.class)
    }

    protected Object createDomain() {
        return new Subject(owner: storeService.account)
    }

    Field createField(Item item, Object propertyId, Component component) {
        String label = getColumnLabel(propertyId)
        if ('isPublic'.equals(propertyId)) {
            def checkBox = new CheckBox(label)
            checkBox.readOnly = !this.storeService.account.isAdmin()
            checkBox
        } else {
            TextField textField = new TextField(label)
            textField.setNullRepresentation('')

            if ('description'.equals(propertyId)) {
                textField.setRows(10)
                textField.setColumns(30)
            }

            textField
        }

    }

    def canInsert() {
        return storeService.account.isNotAnonymous()
    }

    def canEdit(item) {
        return storeService.account.isAdmin() || (storeService.account.isNotAnonymous() && storeService.account.equals(item.getItemProperty('owner').value))
    }

    def canDelete(item) {
        return canEdit(item)
    }

    protected void doRemove(long id) {
        Subject.withTransaction {
            Subject subject = Subject.get(id)
            assert subject

            def owner = subject.owner

            subject.delete()

            //TODO: ��������� ������ �� �������� ������!
            owner.removeFromSubjects(subject)
            storeService.setup.removeFromFilterSubjects(subject)
            if (subject.equals(storeService.setup.activeSubject)) {
                storeService.setup.activeSubject = null
            }
            storeService.setup.merge()

            refresh()
        }
    }

    protected EntityForm createForm() {
        return new SubjectForm()
    }

    class SubjectForm extends EntityForm {

        protected void initButtons(ComponentContainer componentContainer) {
            super.initButtons(componentContainer);

            if (editable) {
                normalizeButton = new Button()
                normalizeButton.caption = i18n('button.normalize.label', 'normalize')
                normalizeButton.addListener(this as Button.ClickListener)
                componentContainer.addComponent(normalizeButton)
            }
        }

        def void buttonClick(ClickEvent clickEvent) {
            if (clickEvent.button == normalizeButton) {
                Long subjectId = getItemDataSource().getItemProperty('id').value as Long

                assert subjectId

                dataService.normalizeSubject(subjectId)

                window.application.mainWindow.showNotification("Normalize is completed")
            } else {
                super.buttonClick(clickEvent)
            }
        }


    }

}
