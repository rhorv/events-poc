package application.domain;

public class Money {

  private Integer amount;
  private Currency currency;

  public Money(Integer amount, Currency currency) {
    this.amount = amount;
    this.currency = currency;
  }

  public Integer getAmount() {
    return amount;
  }

  public Currency getCurrency() {
    return currency;
  }
}
