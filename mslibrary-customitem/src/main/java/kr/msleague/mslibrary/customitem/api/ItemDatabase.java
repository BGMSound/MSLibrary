package kr.msleague.mslibrary.customitem.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.Future;

/**
 * 아이템 데이터베이스 인터페이스입니다.
 * @since 1.0
 * @author Arkarang
 */
public interface ItemDatabase {

    /**
     * 로드 할 수 있는 모든 아이템 데이터를 로드합니다.
     * @return 로드된 아이템 목록
     */
    Future<List<MSItemData>> loadAll();

    /**
     * 직렬화된 아이템을 새로운 아이템으로서 데이터베이스에 등록합니다.
     * @param id 넣고자 하는 아이템 아이디
     * @param item 직렬화된 아이템
     * @return 등록 완료 여부
     */
    Future<Boolean> newItem(int id, @Nonnull MSItemData item);

    /**
     * 아이템 정보를 로드합니다.
     * @param itemID 아이템 아이디
     * @return 직렬화 된 아이템 데이터
     * @throws IllegalStateException 역직렬화 중 오류가 발생할 경우
     */
    Future<MSItemData> load(int itemID) throws IllegalStateException;

    /**
     * 아이템 정보를 삽입합니다.
     * @param itemID 삽입할 아이템 아이디
     * @param item 아이템 이름
     * @return 비동기 완료 여부
     * @throws IllegalArgumentException 직렬화 중 오류가 발생할 경우
     */
    Future<Void> insertItem(int itemID, @Nonnull MSItemData item) throws IllegalArgumentException;

    /**
     * 아이템 정보를 삭제합니다.
     * @param itemID 아이템 고유 번호
     * @return 비동기 완료 여부
     */
    Future<Void> deleteItem(int itemID);

    /**
     * 아이템 정보를 검색합니다.
     * @param path 검색할 경로
     * @param value 검색할 값
     * @return 검색한 경로와 값이 일치하는 아이템 고유 번호 목록
     */
    Future<List<Integer>> search(String path, String value);

    /**
     * 아이템 정보를 수정합니다.
     * @param itemID
     * @param node
     * @param value
     * @return
     */
    Future<Void> modify(int itemID, @Nonnull String node, @Nullable String value);
}
