package dela.ui.subject

import com.vaadin.ui.Window
import dela.context.DataContext
import dela.Subject
import dela.SubjectService

/**
 * @author vedi
 * date 02.07.2010
 * time 23:57:43
 */
class SubjectListWindow extends Window {

    def sessionContext

    def void attach() {

        super.attach()

        def dataContext = new DataContext(sessionContext: sessionContext,
                domainClass: Subject, dataViewName: SubjectService.OWN_AND_PUBLIC_DATA_VIEW)

        def table = new SubjectTable(dataContext: dataContext)
        table.setWidth "100%"
        table.setHeight "100%"
        this.addComponent(table)
        this.content.setWidth "300px"
        this.content.setHeight "350px"
        this.center()
    }
}
