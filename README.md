# Hodao

Helper library for DAOs.  Inspired by the great Hodor himself.  Boilerplate persistence code tends to be like him -- simple and repetitive.

Hodor hodor hodor persist. Hodor hodor hodor merge.  Hodor hodor hodor delete.  Hodor HODOR!

Hodao takes advantage of Apache TomEE's abstract-bean concept.  The DAO is declared abstract and boilerplate methods can be simply annotated and handled by the framework.  Unlike purely interface-based approaches, this still allows you to use plain Java code for persistence logic that falls outside what the framework handles.

Hodao is an extremely simple and small library.  All the guts are in [PersistenceHandler](https://github.com/tomitribe/hodao/blob/master/src/main/java/org/tomitribe/hodao/impl/PersistenceHandler.java).  Use as-is or consider it a jumpstart to writing your own helper API.  The power of simple code is that you can bend it to fit your need -- not the other way around.  Forking heavily encouraged!

Requires Apache TomEE 1.5.x or newer.

## Usage

To use, simply declare your EJB as abstract and implement `java.lang.reflect.InvocationHandler` as follows.

````
import org.tomitribe.hodao.impl.PersistenceHandler;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@Stateless
public abstract class BookCrud implements InvocationHandler {

    @PersistenceContext
    private EntityManager em;

    //...

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return PersistenceHandler.invoke(this.em, method, args);
    }
}
````

From here you can leverage the following annotations to abstract out common `javax.persistence.EntityManager` boilerplate.

- `org.tomitribe.hodao.Find`
- `org.tomitribe.hodao.Merge`
- `org.tomitribe.hodao.Persist`
- `org.tomitribe.hodao.Remove`
- `org.tomitribe.hodao.NamedQuery`
- `org.tomitribe.hodao.QueryString`

All of which map to their simple JPA `EntityManager` equivalent.

### Simple example

````
import org.tomitribe.hodao.Find;
import org.tomitribe.hodao.MaxResults;
import org.tomitribe.hodao.Merge;
import org.tomitribe.hodao.NamedQuery;
import org.tomitribe.hodao.Offset;
import org.tomitribe.hodao.Optional;
import org.tomitribe.hodao.Persist;
import org.tomitribe.hodao.QueryParam;
import org.tomitribe.hodao.impl.PersistenceHandler;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

@Singleton
@Lock(LockType.READ)
public abstract class BookDao implements InvocationHandler {

    @PersistenceContext
    private EntityManager em;

    @Persist
    public abstract Book create(final Book book);

    @Merge
    public abstract Book update(final Book book);

    @Find
    public abstract Book find(Long id);

    @NamedQuery(Book.FIND_BY_TITLE)
    @Optional
    public abstract List<Book> findBooksByTitle(@QueryParam("title") final String title);

    @NamedQuery(Book.FIND_ALL)
    @Optional
    public abstract List<Book> findAll(@Offset final Integer offset, @MaxResults final Integer max);

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return PersistenceHandler.invoke(this.em, method, args);
    }
}
````

### @Persist for `EntityManager.persist`

Valid examples of `@Persist` include:

````
    @Persist
    public abstract Book create(final Book book);

    @Persist
    public abstract void anotherAwesomeMovie(final Movie movie);
````


### @Merge for `EntityManager.merge`

Valid examples of `@Merge` include:

````
    @Merge
    public abstract Book update(final Book book);

    @Merge
    public abstract Color update(final Color color);
````

### @Find for `EntityManager.find`

Valid examples of `@Find` include:

````
    @Find
    public abstract Book find(final Long bookId);

    @Find
    public abstract Author whoIsThis(final long authorId);

    @Find
    public abstract Cover giveMe(final int coverId);

    @Find
    public abstract Color lookFor(final ColorID customPrimaryKey);
````

### @Remove for `EntityManager.remove`

Valid examples of `@Remove` include:

````
    @Remove
    public abstract void delete(final Book book);

    @Remove
    public abstract void rottenTomatoes(final Movie movie);
````
