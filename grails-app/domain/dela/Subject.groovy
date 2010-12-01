package dela

class Subject {

    static belongsTo = [owner : Account]

    String name

    String description
    
    boolean isPublic

    static constraints = {
        description(nullable:true)
    }

    static mapping = {
        owner lazy:false
        versions lazy:false
    }

    def String toString() {
        return name
    }

    boolean equals(o) {
        if (o == null) return false;

        if (this.is(o)) return true;

        if (getClass() != o.class) return false;

        Subject subject = (Subject) o;

        if (id != subject.id) return false;

        return true;
    }

    int hashCode() {
        return (id != null ? id.hashCode() : 0);
    }
}
