package dela.subject

import com.vaadin.ui.Window
import dela.DvTable
import dela.SubjectDvUtils

/**
 * @author vedi
 * date 02.07.2010
 * time 23:57:43
 */
class SubjectListWindow extends Window {

    def domainView = SubjectDvUtils.DEFAULT_DV

    def void attach() {
        this.addComponent new DvTable(domainView: domainView, formFieldFactory: SubjectDvUtils.DEFAULT_FFF)
        this.content.setSizeUndefined()
        this.center()

        super.attach()
    }
}
