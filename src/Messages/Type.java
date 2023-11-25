package Messages;

public enum Type {
    Choke ((byte) 0),
    Unchoke ((byte) 1),
    Interested ((byte) 2),
    NotInterested ((byte) 3),
    Have ((byte) 4),
    BitField ((byte) 5),
    Request ((byte) 6),
    Piece ((byte) 7);

    private final byte type;

    Type (byte type) {
        this.type = type;
    }

    public byte getValue() {
        return type;
    }

    public static Type valueOf (byte b) {
        for (Type t : Type.values()) {
            if (t.type == b) {
                return t;
            }
        }
        throw new IllegalArgumentException();
    }

}
