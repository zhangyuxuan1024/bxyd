package net.iclassmate.bxyd.bean.owner;

/**
 * Created by xydbj on 2016.7.30.
 */
public class SpaceInfo {
    private Authority authority;

    @Override
    public String toString() {
        return "SpaceInfo{" +
                "authority=" + authority +
                '}';
    }

    public Authority getAuthority() {
        return authority;
    }

    public void setAuthority(Authority authority) {
        this.authority = authority;
    }
}
