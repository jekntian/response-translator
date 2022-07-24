package online.kakapapa.vo;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tien.Chang
 */
public class PaginateVO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 总数据行数
     */
    private Integer totalRow;
    /**
     * 总页数
     */
    private Integer totalPage;
    /**
     * 每页数据行数
     */
    private Integer pageSize;
    /**
     * 当前页数
     */
    private Integer currPage;
    /**
     * 列表数据
     */
    private List<?> list;

    /**
     * 实例化分页对象
     *
     * @param list       列表数据
     * @param totalCount 总记录数
     * @param pageSize   每页记录数
     * @param currPage   当前页数
     */
    public PaginateVO(List<?> list, int totalCount, int pageSize, int currPage) {
        this.list = list;
        this.totalRow = totalCount;
        this.pageSize = pageSize;
        this.currPage = currPage;
        this.totalPage = (int) Math.ceil((double) totalCount / pageSize);
    }

    public int getTotalRow() {
        return totalRow;
    }

    public void setTotalRow(int totalRow) {
        this.totalRow = totalRow;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getCurrPage() {
        return currPage;
    }

    public void setCurrPage(int currPage) {
        this.currPage = currPage;
    }

    public List<?> getList() {
        return list;
    }

    public void setList(List<?> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "PaginateVO{" +
                "totalRow=" + totalRow +
                ", totalPage=" + totalPage +
                ", pageSize=" + pageSize +
                ", currPage=" + currPage +
                ", list=" + list +
                '}';
    }
}
