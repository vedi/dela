package dela

import com.vaadin.data.Item
import com.vaadin.data.util.BeanItem
import com.vaadin.event.ItemClickEvent
import com.vaadin.ui.Button
import com.vaadin.ui.Button.ClickEvent
import com.vaadin.ui.Button.ClickListener
import com.vaadin.ui.Form
import com.vaadin.ui.FormFieldFactory
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Table
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.Window
import dela.container.DomainContainer
import dela.container.DomainMeta
import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer

/**
 * @author vedi
 * date 28.06.2010
 * time 18:11:29
 */
class DvTable extends VerticalLayout implements ClickListener {

    def DomainMeta domainView
    def FormFieldFactory formFieldFactory

    Table table

    Button addButton
    Button editButton
    Button removeButton
    Button refreshButton

    private LazyQueryContainer container

    def saveHandler = {item ->
        domainView.domainClass.withTransaction {
            Long id = item.getItemProperty("id")?.value as Long
            def domain
            if (id) {
                domain = domainView.domainClass.get(id)
                assert domain
            } else {
                domain = domainView.domainClass.newInstance()
            }
            item.getItemPropertyIds().each {
                if (!'id'.equals(it)) {
                    domain[it] = item.getItemProperty(it).value;
                }
            }
            if (!domain.save()) {
                domain.errors.each {
                    println it
                }
            }
        }
        this.refresh()
    }

    def DvTable() {
        table = new Table()
        initToolBar()
    }

    def setDropHandler(dropHandler) {
        table.dropHandler = dropHandler
    }
    
    def getContainerDataSource() {
        table.containerDataSource
    }

    def setContainerDataSource(containerDataSource) {
        table.containerDataSource = containerDataSource
    }

    @Override
    public void attach() {
        initTable()

        super.attach()

    }

    private def initToolBar() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();

        addButton = new Button("add", this);
        horizontalLayout.addComponent addButton

        editButton = new Button("edit", this);
        horizontalLayout.addComponent editButton

        removeButton = new Button("remove", this);
        horizontalLayout.addComponent removeButton

        refreshButton = new Button("refresh", this);
        horizontalLayout.addComponent refreshButton

        this.addComponent horizontalLayout
    }

    private def initTable() {
        this.table.immediate = true
        this.table.selectable = true
        this.table.nullSelectionAllowed = false

        assert domainView

        if (domainView.caption) {
            this.caption = domainView.caption
        }

        this.table.addListener(new ItemClickEvent.ItemClickListener() {
            public void itemClick(ItemClickEvent event) {
                if (event.doubleClick) {
                    showForm(event.item)
                }
            }
        })

        container = new DomainContainer(domainView)

        this.table.setContainerDataSource(this.container)

        def visibleColumns = domainView.gridVisible
        this.table.setVisibleColumns(visibleColumns as Object[]);
        this.table.setColumnHeaders(visibleColumns.collect {domainView.getDef(it)?.label ?: it} as String[])
        visibleColumns.each {
            this.table.setColumnWidth it, domainView.getColumnWidth(it)
        }

        this.table.setDragMode(Table.TableDragMode.ROW);
        this.table.setSizeFull()

        this.addComponent(this.table)
    }

    public void refresh() {
        container.refresh()
    }

    void buttonClick(ClickEvent clickEvent) {
        if (clickEvent.button == addButton) {
            showForm(new BeanItem(applyTemplate(domainView.domainClass.newInstance())))
            refresh()                           
        } else if (clickEvent.button == editButton) {
            if (table.value) {
                showForm(container.getItem(table.value))
                refresh()
            }
        } else if (clickEvent.button == removeButton) {
            if (table.value) {
                remove(container.getItem(table.value))
                refresh()
            }
        } else if (clickEvent.button == refreshButton) {
            refresh()
        }
    }

    def applyTemplate(domain) {
        domain
    }

    void remove(Item item) {
        this.window.application.mainWindow.addWindow(new YesNoDialog(
                "confirm delete",
                "are you sure?",
                new YesNoDialog.Callback() {
                    public void onDialogResult(boolean happy) {
                        Long id = item.getItemProperty("id")?.value as Long
                        assert id
                        domainView.domainClass.withTransaction {
                            def domain = domainView.domainClass.get(id)
                            assert domain

                            domain.delete()
                            refresh()
                        }
                    }
                }))
    }

    void showForm(selectedItem) {
        Window window = new Window(domainView.domainClass.simpleName)

        Form form = createForm()

        form.formFieldFactory = formFieldFactory
        form.itemDataSource =  selectedItem
        form.visibleItemProperties = domainView.editVisible
        form.saveHandler = saveHandler

        window.addComponent(form)

        window.layout.setSizeUndefined()
        window.center()

        this.window.application.mainWindow.addWindow window
    }

    protected Form createForm() {
        return new DomainForm()
    }

    def String getDescription() {
        return "Static desc"
    }


}
