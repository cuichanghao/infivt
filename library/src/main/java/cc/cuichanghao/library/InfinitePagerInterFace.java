package cc.cuichanghao.library;


public interface InfinitePagerInterFace {
    void willBePageSelect(int selectPosition);
    int getRealCount();
    int getCenterPosition();
}
