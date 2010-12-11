package dela

class Setup {

    Subject activeSubject

    static belongsTo = [account : Account] 
    static hasMany = [filterSubjects: Subject, filterStates: State]

    static mapping = {
        account lazy:false
        activeSubject lazy:false
        filterSubjects lazy:false
        filterStates lazy:false
    }

    static constraints = {
        activeSubject(nullable:true)
        account(unique: true)
    }


}
