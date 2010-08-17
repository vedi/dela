package dela.ui.subject

import com.vaadin.ui.Window

/**
 * @author vedi
 * date 02.07.2010
 * time 23:57:43
 */
class SubjectListWindow extends Window implements Serializable {

    def metaDomain

    def void attach() {

        super.attach()

        
        def table = new SubjectTable(metaDomain: metaDomain)
        table.setWidth "100%"
        table.setHeight "100%"
        this.addComponent(table)
        this.content.setWidth "300px"
        this.content.setHeight "350px"
        this.center()
    }
}
