package org.wso2.balana.attr;

import java.util.Iterator;
import java.util.NoSuchElementException;

class BagAttribute$ImmutableIterator
  implements Iterator
{
  private Iterator iterator;
  
  public BagAttribute$ImmutableIterator(Iterator iterator)
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

/* Location:
 * Qualified Name:     org.wso2.balana.attr.BagAttribute.ImmutableIterator
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */