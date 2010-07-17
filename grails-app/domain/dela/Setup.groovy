package dela

class Setup {

    Account account 

    Subject activeSubject

    static hasMany = [filterSubjects: Subject, filterStates: State]

    static mapping = {
        activeSubject lazy:false
        filterSubjects lazy:false
        filterStates lazy:false
    }

    static constraints = {
        activeSubject(nullable:true)
    }
}
