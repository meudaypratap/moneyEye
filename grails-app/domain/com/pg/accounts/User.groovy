package com.pg.accounts

class User {

    transient springSecurityService

    String username
    String email
    String password
    boolean enabled
    boolean accountExpired
    boolean accountLocked
    boolean passwordExpired
    Date dateCreated
    Date lastUpdated

    static constraints = {
        username blank: false, unique: true
        password blank: false, password: true
        email blank: true, nullable: true, email: true, unique: true
    }

    static mapping = {
        password column: '`password`'
    }

    Set<Role> getAuthorities() {
        UserRole.findAllByUser(this).collect { it.role } as Set
    }

    def beforeInsert() {
        encodePassword()
    }

    def beforeUpdate() {
        if (isDirty('password')) {
            encodePassword()
        }
    }

    protected void encodePassword() {
        password = springSecurityService.encodePassword(password)
    }

    List<User> fetchFriends(){
        return Friend.createCriteria().list{
            projections {
                property('friend')
                property('createdBy')
            }
            or{
                eq('createdBy',this)
                eq('friend',this)
            }
        }.flatten().unique().sort{it.username}
    }

    String toString() {
        return username
    }
}
