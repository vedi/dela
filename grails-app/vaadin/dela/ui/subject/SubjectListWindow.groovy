package dela.ui.subject

import com.vaadin.ui.Window

/**
 * @author vedi
 * date 02.07.2010
 * time 23:57:43
 */
class SubjectListWindow extends Window {

    def metaDomain

    def void attach() {

        super.attach()

        this.addComponent(new SubjectTable(metaDomain: metaDomain))
        this.content.setSizeUndefined()
        this.center()
    }
}
