package com.jcondotta.cards.core.service.cache;

class CardsCacheKeyTest {

//    private static final UUID CARD_ID_JEFFERSON = TestCard.JEFFERSON.getCardId();
//
//    @Test
//    void shouldThrowNullPointerException_whenCardIdIsNull() {
//        var exception = assertThrows(NullPointerException.class, () -> new CardsCacheKey(null));
//
//        assertThat(exception)
//                .satisfies(violation -> assertThat(violation.getMessage())
//                        .isEqualTo("cache.cards.cardId.notNull"));
//    }
//
//    @Test
//    void shouldReturnCacheKey_whenValidCardIdIsProvided() {
//        var cardsCacheKey = new CardsCacheKey(CARD_ID_JEFFERSON);
//        var expectedCacheKey = buildExpectedCacheKey(CARD_ID_JEFFERSON);
//
//        assertThat(cardsCacheKey.getKey()).isEqualTo(expectedCacheKey);
//    }
//
//    private String buildExpectedCacheKey(UUID cardId){
//        return new StringJoiner(":")
//                .add("cards")
//                .add("card-id:" + cardId)
//                .toString();
//    }
}