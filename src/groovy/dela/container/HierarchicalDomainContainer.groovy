package dela.container

import com.vaadin.data.Container

/**
 * @author vedi
 * date 02.08.2010
 * time 8:38:58
 */
class HierarchicalDomainContainer extends DomainLazyContainer implements Container.Hierarchical {

    boolean areChildrenAllowed(Object itemId) {
        return true
    }

    Collection<?> getChildren(Object itemId) {
        return null;  // TODO: Something smarter?
    }

    Object getParent(Object itemId) {
        return null;  // TODO: Something smarter?
    }

    boolean hasChildren(Object itemId) {
        return false;  // TODO: Something smarter?
    }

    boolean isRoot(Object itemId) {
        return getParent(itemId) == null
    }

    Collection<?> rootItemIds() {
        return null;  // TODO: Something smarter?
    }

    boolean setChildrenAllowed(Object itemId, boolean areChildrenAllowed) {
        throw new UnsupportedOperationException()
    }

    boolean setParent(Object itemId, Object newParentId) {
        throw new UnsupportedOperationException()
    }
}
