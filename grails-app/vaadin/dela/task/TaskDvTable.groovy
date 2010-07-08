package dela.task

import com.vaadin.ui.Form
import dela.DvTable
import dela.State

/**
 * @author vedi
 * date 04.07.2010
 * time 20:29:22
 */
class TaskDvTable extends DvTable {

    def storeService

    def TaskDvTable() {
        storeService = getBean(StoreService.class)
    }

    protected Form createForm() {
        return new TaskDomainForm()
    }

    def applyTemplate(task) {
        task.subject = storeService.setup.activeSubject
        task.state = State.findAll()[0];
        return super.applyTemplate(task);
    }


}
