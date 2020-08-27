package uk.gov.cshr.civilservant.dto.factory;

public abstract class DtoFactory<K, T> {

  public abstract K create(T entity);
}
