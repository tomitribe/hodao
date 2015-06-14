# Hodaor

**H**elper library for **DAO**s

*... other letters added for fun.  HODOR!*

## Description

Hodaor takes advantage of Apache TomEE's abstract-bean concept.  The DAO is declared abstract and boilerplate methods can be simply annotated and handled by the framework.  Unlike purely interface-based approaches, this still allows you to use plain Java code for persistence logic that falls outside what the framework handles.

## Usage

To use, simply declare your EJB as abstract and implement `java.lang.reflect.InvocationHandler` as follows.

````
import org.tomitribe.hodaor.impl.PersistenceHandler;
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

- `org.tomitribe.hodaor.Find`
- `org.tomitribe.hodaor.Merge`
- `org.tomitribe.hodaor.Persist`
- `org.tomitribe.hodaor.Remove`
- `org.tomitribe.hodaor.NamedQuery`

All of which map to their simple JPA `EntityManager` equivalent.


### @Persist for `EntityManager.persist`

````
    @Persist
    public abstract Book create(final Book book);

````

Performs a null check on `Book` followed by a `em.persist(book);`

If `Book` is null a `org.tomitribe.hodaor.ValidationException`

### @Merge for `EntityManager.merge`

The following common boilerplate

````
    public Book update(final Book book) {
        if (book == null) {
            throw new ValidationException("Book object is null");
        }

        return em.merge(book);
    }
````

Can be replaced with a simple:

````
    @Merge
    public abstract Book update(final Book book);
````

### @Find for `EntityManager.find`

The following common boilerplate

````
    public Book find(final Book book) {
        if (book == null) {
            throw new ValidationException("Book object is null");
        }

        return em.merge(book);
    }
````

Can be replaced with a simple:

````
    @Merge
    public abstract Book update(final Book book);
````


