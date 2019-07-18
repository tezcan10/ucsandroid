package org.wso2.balana.attr;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class BagAttribute
  extends AttributeValue
{
  private Collection bag;
  
  public BagAttribute(URI type, Collection bag)
  {
    super(type);
    if (type == null) {
      throw new IllegalArgumentException("Bags require a non-null type be provided");
    }
    if ((bag == null) || (bag.size() == 0))
    {
      this.bag = new ArrayList();
    }
    else
    {
      Iterator it = bag.iterator();
      while (it.hasNext())
      {
        AttributeValue attr = (AttributeValue)it.next();
        if (attr.isBag()) {
          throw new IllegalArgumentException("bags cannot contain other bags");
        }
        if (!type.equals(attr.getType())) {
          throw new IllegalArgumentException("Bag items must all be of the same type");
        }
      }
      this.bag = bag;
    }
  }
  
  public boolean isBag()
  {
    return true;
  }
  
  public static BagAttribute createEmptyBag(URI type)
  {
    return new BagAttribute(type, null);
  }
  
  public boolean isEmpty()
  {
    return bag.size() == 0;
  }
  
  public int size()
  {
    return bag.size();
  }
  
  public boolean contains(AttributeValue value)
  {
    return bag.contains(value);
  }
  
  public boolean containsAll(BagAttribute bag)
  {
    return this.bag.containsAll(bag);
  }
  
  public Iterator iterator()
  {
    return new ImmutableIterator(bag.iterator());
  }
  
  private static class ImmutableIterator
    implements Iterator
  {
    private Iterator iterator;
    
    public ImmutableIterator(Iterator iterator)
    {
      this.iterator = iterator;
    }
    
    public boolean hasNext()
    {
      return iterator.hasNext();
    }
    
    public Object next()
      throws NoSuchElementException
    {
      return iterator.next();
    }
    
    public void remove()
      throws UnsupportedOperationException
    {
      throw new UnsupportedOperationException();
    }
  }
  
  public String encode()
  {
    throw new UnsupportedOperationException("Bags cannot be encoded");
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.attr.BagAttribute
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */