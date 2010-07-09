package dela.ui.common

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
import dela.YesNoDialog
import dela.container.DomainContainer
import dela.meta.MetaDomain
import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer

/**
 * @author vedi
 * date 28.06.2010
 * time 18:11:29
 */
public class EntityTable extends VerticalLayout implements ClickListener {

    def MetaDomain metaDomain

    Table table

    HorizontalLayout toolBarLayout

    Button addButton
    Button editButton
    Button removeButton
    Button refreshButton

    protected LazyQueryContainer container

    def formFieldFactory

    def counter = {
        metaDomain.domainClass.count()
    }

    def selector = {startIndex, count, sortProperty, ascendingState ->
        if (sortProperty) {
            metaDomain.domainClass.list(offset:startIndex,  max:count, sort:sortProperty, order:ascendingState)
        } else {
            metaDomain.domainClass.list(offset:startIndex,  max:count)
        }
    }

    def saveHandler = {item ->
        metaDomain.domainClass.withTransaction {
            Long id = item.getItemProperty("id")?.value as Long
            def domain
            if (id) {
                domain = metaDomain.domainClass.get(id)
                assert domain
            } else {
                domain = metaDomain.domainClass.newInstance()
            }
            item.getItemPropertyIds().each {
                if (!'id'.equals(it)) {
                    domain[it] = item.getItemProperty(it).value;
                }
            }
            def result = domain.save()
            assert result
        }
        this.refresh()
    }

    def EntityTable() {
        table = new Table()
        initToolBar()
        this.addComponent toolBarLayout
    }

    def setDropHandler(dropHandler) {
        table.dropHandler = dropHandler
    }

    def getContainerDataSource() {
        table.containerDataSource
    }
    
    @Override
    public void attach() {

        assert metaDomain

        initTable()

        super.attach()

    }

    protected void initToolBar() {
        toolBarLayout = new HorizontalLayout();

        addButton = new Button("add", this);
        toolBarLayout.addComponent addButton

        editButton = new Button("edit", this);
        toolBarLayout.addComponent editButton

        removeButton = new Button("remove", this);
        toolBarLayout.addComponent removeButton

        refreshButton = new Button("refresh", this);
        toolBarLayout.addComponent refreshButton
    }

    def setContainerDataSource(containerDataSource) {
        table.containerDataSource = containerDataSource
    }

    private def initTable() {

        this.table.immediate = true
        this.table.selectable = true
        this.table.nullSelectionAllowed = false

        if (metaDomain.caption) {
            this.caption = metaDomain.caption
        }

        this.table.addListener(new ItemClickEvent.ItemClickListener() {
            public void itemClick(ItemClickEvent event) {
                if (event.doubleClick) {
                    showForm(event.item)
                }
            }
        })

        container = new DomainContainer(metaDomain.domainClass, getSelector(), getCounter(), metaDomain.columns)

        this.table.setContainerDataSource(this.container)

        this.table.setVisibleColumns(getGridVisibleColumns() as Object[]);
        this.table.setColumnHeaders(getColumnHeaders())

        initGrid();

        this.table.setSizeFull()

        this.addComponent(this.table)
    }

    def initGrid() {
    }

    public void refresh() {
        container.refresh()
    }

    void buttonClick(ClickEvent clickEvent) {
        if (clickEvent.button == addButton) {
            showForm(new BeanItem(createDomain()))
            refresh()                           
        } else if (clickEvent.button == editButton) {
            def item = container.getItem(table.value)
            if (item) {
                showForm(item)
                refresh()
            }
        } else if (clickEvent.button == removeButton) {
            def item = container.getItem(table.value)
            if (item) {
                remove(item)
                refresh()
            }
        } else if (clickEvent.button == refreshButton) {
            refresh()
        }
    }

    void remove(Item item) {
        this.window.application.mainWindow.addWindow(new YesNoDialog(
                "confirm delete",
                "are you sure?",
                new YesNoDialog.Callback() {
                    public void onDialogResult(boolean happy) {
                        Long id = item.getItemProperty("id")?.value as Long
                        assert id
                        doRemove(id)
                    }

                }))
    }

    private def doRemove(long id) {
        metaDomain.domainClass.withTransaction {
            def domain = metaDomain.domainClass.get(id)
            assert domain

            domain.delete()
            refresh()
        }
    }
    void showForm(selectedItem) {
        Window window = new Window(metaDomain.domainClass.simpleName)

        Form form = createForm()

        if (getFormFieldFactory()) {
            form.formFieldFactory = getFormFieldFactory() as FormFieldFactory
        }
        form.itemDataSource =  selectedItem
        form.visibleItemProperties = getEditVisibleColumns()
        form.saveHandler = saveHandler

        window.addComponent(form)

        window.layout.setSizeUndefined()
        window.center()

        this.window.application.mainWindow.addWindow window
    }

    protected List<String> getGridVisibleColumns() {
        return metaDomain.columns.collect {it.field}
    }

    protected List<String> getEditVisibleColumns() {
        metaDomain.columns.collect {it.field}
    }

    protected Form createForm() {
        return new EntityForm()
    }

    protected createDomain() {
        return metaDomain.domainClass.newInstance()
    }

    protected String[] getColumnHeaders() {
        return getGridVisibleColumns().collect {metaDomain.getMetaColumn(it)?.label ?: it} as String[]
    }

}
