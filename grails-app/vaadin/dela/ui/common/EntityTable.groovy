package dela.ui.common

import com.vaadin.data.Item
import com.vaadin.data.util.BeanItem
import com.vaadin.event.ItemClickEvent
import com.vaadin.event.ShortcutAction
import com.vaadin.event.ShortcutListener
import com.vaadin.terminal.FileResource
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
    Button deleteButton
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
            boolean isNew = id == null
            def domain
            if (isNew) {
                domain = metaDomain.domainClass.newInstance()
            } else {
                domain = metaDomain.domainClass.get(id)
                assert domain
            }
            item.getItemPropertyIds().each {
                if (!'id'.equals(it)) {
                    domain[it] = item.getItemProperty(it).value;
                }
            }
            def result = domain.save()

            assert result

            this.refresh()
            if (isNew) {
                this.select(domain.id)
            }
        }
    }

    def EntityTable() {
        table = new Table()
    }

    def setDropHandler(dropHandler) {
        table.dropHandler = dropHandler
    }

    @Override
    public void attach() {

        assert metaDomain

        table.addShortcutListener(new ShortcutListener("down", ShortcutAction.KeyCode.ARROW_DOWN, new int[0]) {
            void handleAction(Object o, Object o1) {
                EntityTable.this.table.select(EntityTable.this.table.nextItemId(EntityTable.this.table.getValue()))
            }
        });
        table.addShortcutListener(new ShortcutListener("up", ShortcutAction.KeyCode.ARROW_UP, new int[0]) {
            void handleAction(Object o, Object o1) {
                EntityTable.this.table.select(EntityTable.this.table.prevItemId(EntityTable.this.table.getValue()))
            }
        });

        initToolBar()
        this.addComponent toolBarLayout

        initTable()

        super.attach()

    }

    protected void initToolBar() {
        toolBarLayout = new HorizontalLayout();

        addButton = new Button();
        addButton.setDescription("add")
        addButton.setClickShortcut(ShortcutAction.KeyCode.INSERT)
        addButton.setIcon(new FileResource(new File('web-app/images/skin/database_add.png'), this.window.application))
        addButton.addListener(this as ClickListener)
        toolBarLayout.addComponent addButton

        editButton = new Button();
        editButton.setDescription("edit")
        editButton.setClickShortcut(ShortcutAction.KeyCode.ENTER)
        editButton.setIcon(new FileResource(new File('web-app/images/skin/database_edit.png'), this.window.application))
        editButton.addListener(this as ClickListener)
        toolBarLayout.addComponent editButton

        deleteButton = new Button();
        deleteButton.setDescription("delete")
        deleteButton.setClickShortcut(ShortcutAction.KeyCode.DELETE)
        deleteButton.setIcon(new FileResource(new File('web-app/images/skin/database_delete.png'), this.window.application))
        deleteButton.addListener(this as ClickListener)
        toolBarLayout.addComponent deleteButton

        refreshButton = new Button();
        refreshButton.setDescription("refresh")
        refreshButton.setIcon(new FileResource(new File('web-app/images/skin/database_refresh.png'), this.window.application))
        refreshButton.addListener(this as ClickListener)
        toolBarLayout.addComponent refreshButton
    }

    def setContainerDataSource(containerDataSource) {
        table.containerDataSource = containerDataSource
    }

    protected void initTable() {

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
        } else if (clickEvent.button == editButton) {
            def item = container.getItem(table.value)
            if (item) {
                showForm(item)
            }
        } else if (clickEvent.button == deleteButton) {
            def item = container.getItem(table.value)
            if (item) {
                remove(item)
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
                    public void onDialogResult(boolean yes) {
                        if (yes) {
                            Long id = item.getItemProperty("id")?.value as Long
                            assert id
                            doRemove(id)
                        }
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

    protected select(Long id) {
        def itemIds = this.container.getItemIds()
        for (itemId in itemIds) {
            if (this.container.getItem(itemId).getItemProperty('id').value.equals(id)) {
                this.table.setValue(itemId)
                break
            }
        }
    }
}
