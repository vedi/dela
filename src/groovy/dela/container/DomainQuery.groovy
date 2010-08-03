package dela.container;


import com.vaadin.data.Item
import com.vaadin.data.util.BeanItem
import org.vaadin.addons.lazyquerycontainer.Query
import org.vaadin.addons.lazyquerycontainer.QueryDefinition

public class DomainQuery implements Query {

    def domainClass
    def createSelectCriteria
    def selector
    def counter

	private QueryDefinition definition
    private String sortProperty
    private String ascendingState

	public DomainQuery(domainClass, selector, counter,
                       QueryDefinition definition, Object[] sortPropertyIds, boolean[] ascendingStates) {

        this.domainClass = domainClass
        this.selector = selector
        this.counter = counter

		this.definition = definition
        
        assert sortPropertyIds.length <= 1
        this.sortProperty = sortPropertyIds.size() > 0 ? sortPropertyIds[0] : null
        this.ascendingState = ascendingStates.size() > 0 ? (ascendingStates[0] ? 'asc' : 'desc') : null
	}

    @Override
	public List<Item> getItems(int startIndex, int count) {
        domainClass.withTransaction {
            def list = selector(startIndex, count, sortProperty, ascendingState)

            return list.collect {
                new BeanItem(it)
            }
        }
	}

	@Override
	public int size() {
        return counter()
	}

}