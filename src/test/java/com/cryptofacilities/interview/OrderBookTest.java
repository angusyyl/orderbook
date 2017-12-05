package com.cryptofacilities.interview;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

public class OrderBookTest {

	OrderBook ob;
	final String instrument = "bond";
	final long priceLevel1 = 300;
	final long priceLevel2 = 50;
	final long priceLevel3 = 120;
	final long priceLevel4 = 788;

	Order newOrder1;
	Order newOrder2;
	Order newOrder3;
	Order newOrder4;
	Order newOrder5;
	Order newOrder6;
	Order newOrder7;

	@Before
	public void initialize() {
		ob = new OrderBook(instrument);
		newOrder1 = new Order("B112", instrument, Side.sell, priceLevel1, 20);
		newOrder2 = new Order("B115", instrument, Side.sell, priceLevel2, 80);
		newOrder3 = new Order("B118", instrument, Side.sell, priceLevel1, 10);
		ob.addOrder(newOrder1);
		ob.addOrder(newOrder2);
		ob.addOrder(newOrder3);

		newOrder4 = new Order("LA30", instrument, Side.buy, priceLevel1, 30);
		newOrder5 = new Order("LA31", instrument, Side.buy, priceLevel1, 20);
		newOrder6 = new Order("LA32", instrument, Side.buy, priceLevel4, 80);
		newOrder7 = new Order("LA33", instrument, Side.buy, priceLevel4, 100);
		ob.addOrder(newOrder4);
		ob.addOrder(newOrder5);
		ob.addOrder(newOrder6);
		ob.addOrder(newOrder7);
	}

	@Test
	public void testAddPriceLevel() {
		Order newSellOrder = new Order("B222", instrument, Side.sell, 999, 20);
		ob.addOrder(newSellOrder);

		assertNotNull("Sell order at price 999 is not null", ob.getPriceLevel(999, Side.sell));

		Order newBuyOrder = new Order("B444", instrument, Side.buy, 123, 3);
		Order newBuyOrder2 = new Order("B445", instrument, Side.buy, 123, 5);
		ob.addOrder(newBuyOrder);
		ob.addOrder(newBuyOrder2);

		long expectedOrderNum = 2;
		long actualOrderNum = ob.getPriceLevel(123, Side.buy).getOrderNum();
		assertEquals("Order num at price 123 for buy side is 2", expectedOrderNum, actualOrderNum);
	}

	@Test
	public void testDeletePriceLevel() {
		ob.deleteOrder(newOrder6.getOrderId());
		ob.deleteOrder(newOrder7.getOrderId());

		assertNull("Price level of 788 for buy orders is null", ob.getPriceLevel(788, Side.buy));
	}

	@Test
	public void testGetPriceLevel() {
		long expectedPriceAtLv2 = 50;
		long actualPriceAtLv2 = ob.getPriceLevel(priceLevel2, Side.sell).getPrice();
		assertEquals("Price for lv2 for sell side is 50", expectedPriceAtLv2, actualPriceAtLv2);

		long expectedTotalVolAtLv1 = 9000;
		long actualTotalVolAtLv1 = ob.getTotalVolumeAtLevel(Side.sell, priceLevel1);
		assertEquals("Total volume at Lv1 for sell side is 9000", expectedTotalVolAtLv1, actualTotalVolAtLv1);
	}

	@Test
	public void testAddOrder() {
		Order newOrder = new Order("B144", instrument, Side.sell, priceLevel1, 40);
		ob.addOrder(newOrder);

		long expectedQtyAtLv1 = 70;
		long actualQtyAtLv1 = ob.getTotalQuantityAtLevel(Side.sell, priceLevel1);
		assertEquals("Total quantity at level 1 is 70", expectedQtyAtLv1, actualQtyAtLv1);

		long expectedQtyAtLv2 = 80;
		long actualQtyAtLv2 = ob.getTotalQuantityAtLevel(Side.sell, priceLevel2);
		assertEquals("Total quantity at level 2 is 80", expectedQtyAtLv2, actualQtyAtLv2);

		long expectedQtyAtLv2ForBuySide = 0;
		long actualQtyAtLv2ForBuySide = ob.getTotalQuantityAtLevel(Side.buy, priceLevel2);
		assertEquals("Total quantity at level 2 for Buy side is 0", expectedQtyAtLv2ForBuySide,
				actualQtyAtLv2ForBuySide);
	}

	@Test
	public void testModifyOrder() {
		//test changed quantity
		ob.modifyOrder(newOrder2.getOrderId(), 50);

		long expectedNewQty = 50;
		long actualNewQty = newOrder2.getQuantity();
		assertEquals("New quantity is 50", expectedNewQty, actualNewQty);

		//test increased quantity
		ob.modifyOrder(newOrder4.getOrderId(), 50);
		
		Order secondOrder = ob.getPriceLevel(priceLevel1, Side.buy).getOrders().get(1);
		assertEquals("newOrder4 is at end of queue", newOrder4, secondOrder);
		
		Order firstOrder = ob.getPriceLevel(priceLevel1, Side.buy).getOrders().get(0);
		assertEquals("newOrder5 is at begin of queue", newOrder5, firstOrder);

		//test decreased quantity
		ob.modifyOrder(newOrder1.getOrderId(), 5);
		
		secondOrder = ob.getPriceLevel(priceLevel1, Side.sell).getOrders().get(1);
		assertEquals("newOrder3 is at end of queue", newOrder3, secondOrder);
		
		firstOrder = ob.getPriceLevel(priceLevel1, Side.sell).getOrders().get(0);
		assertEquals("newOrder1 is at begin of queue", newOrder1, firstOrder);
	}

	@Test
	public void testDeleteOrder() {
		ob.deleteOrder(newOrder2.getOrderId());
		ob.deleteOrder(newOrder4.getOrderId());

		int expectedSellPriceLvNum = 1;
		int actualSellPriceLvNum = ob.getSellPriceLevels().keySet().size();
		assertEquals("Price level remains for sell orders is 1", expectedSellPriceLvNum, actualSellPriceLvNum);

		long expectedBuyOrderNum = 3;
		long actualBuyOrderNum = 0;
		for (PriceLevel pl : ob.getBuyPriceLevels().values()) {
			actualBuyOrderNum += pl.getOrderNum();
		}
		assertEquals("Buy orders num remains for buy orders is 3", expectedBuyOrderNum, actualBuyOrderNum);
	}

	@Test
	public void testGetBestPrice() {
		long expectedBestBuyPrice = 788;
		long actualBestBuyPrice = ob.getBestPrice(Side.buy);
		assertEquals("Best buy price is 788", expectedBestBuyPrice, actualBestBuyPrice);

		long expectedBestSellPrice = 300;
		long actualBestSellPrice = ob.getBestPrice(Side.sell);
		assertEquals("Best sell price is 300", expectedBestSellPrice, actualBestSellPrice);
	}

	@Test
	public void testGetOrderNumAtLevel() {
		long expectedOrderNumAtLv4 = 2;
		long actualOrderNumAtLv4 = ob.getOrderNumAtLevel(Side.buy, 788);
		assertEquals("Order number is 2 at 788", expectedOrderNumAtLv4, actualOrderNumAtLv4);

		long expectedOrderNumAtLv5 = 0;
		long actualOrderNumAtLv5 = ob.getOrderNumAtLevel(Side.sell, 0);
		assertEquals("Order number is 0 at priceLevel5", expectedOrderNumAtLv5, actualOrderNumAtLv5);
	}

	@Test
	public void testGetTotalQuantitytLevel() {
		long expectedTotalQtyAtLv4 = 180;
		long actualTotalQtyAtLv4 = ob.getTotalQuantityAtLevel(Side.buy, 788);
		assertEquals("Total quantity is 180 at price 788 for buy side", expectedTotalQtyAtLv4, actualTotalQtyAtLv4);

		long expectedTotalQtyAtLv5 = 0;
		long actualTotalQtyAtLv5 = ob.getTotalQuantityAtLevel(Side.sell, 0);
		assertEquals("Total quantity is 0 at price 0 for sell side", expectedTotalQtyAtLv5, actualTotalQtyAtLv5);
	}

	@Test
	public void testGetTotalVolumetLevel() {
		long expectedTotalVolAtLv4 = 141840;
		long actualTotalVolAtLv4 = ob.getTotalVolumeAtLevel(Side.buy, 788);
		assertEquals("Total volume is 141840 at price 788 for buy side", expectedTotalVolAtLv4, actualTotalVolAtLv4);

		long expectedTotalVolAtLv5 = 0;
		long actualTotalVolAtLv5 = ob.getTotalVolumeAtLevel(Side.sell, 0);
		assertEquals("Total volume is 0 at price 0 for sell side", expectedTotalVolAtLv5, actualTotalVolAtLv5);
	}
	
	@Test
	public void testGetOrders() {
		Order newOrderA = new Order("B112", instrument, Side.sell, priceLevel1, 20);
		Order newOrderB = new Order("B118", instrument, Side.sell, priceLevel1, 10);
		
		Order[] expectedOrders = {newOrderA, newOrderB};
		assertArrayEquals(expectedOrders, ob.getOrdersAtLevel(Side.sell, priceLevel1).toArray());
	}
}
