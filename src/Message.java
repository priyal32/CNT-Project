import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;

public class Message {

    public Message(Type type)
    {
        this (type, null);
    }

    // Message has payload then length is payload + 1 else is 0
    public Message(Type type, byte[] payload)
    {
        if(payload == null)
        {
            length = 0;
        }
        else
        {
            length = payload.length + 1;
        }
        this.type = type;
        this.payload = payload;
    }

    public Type getType()
    {
        return type;
    }

    // return Instance of Message Type
    public Message getMessageInstance( Type type)
    {
        if (type == Type.Choke)
        {
            return new Choke();
        }
        else if (type == Type.Unchoke)
        {
            return new Unchoke();
        }
        else if (type == Type.Interested)
        {
            return new Interested();
        }
        else if (type == Type.NotInterested)
        {
            return new Choke();
        }
        else if (type == Type.Have)
        {
            return new Choke();
        }
        else if (type == Type.BitField)
        {
            return new Choke();
        }
        else if (type == Type.Request)
        {
            return new Choke();
        } else if (type == Type.Piece)
        {
            return new Choke();
        }
        return null;
    }

    public void readMessage(ObjectInputStream in) throws IOException
    {
        if(this.type == Type.BitField)
        {
            if(payload != null && payload.length > 0)
            {
                System.out.println(in.read(payload, 0, payload.length));

            }

        }

    }

    public void writeMessage(ObjectOutputStream out) throws IOException
    {
        out.writeInt(length);
        out.writeByte(type.getValue());
        if(payload != null && payload.length > 0)
        {
            out.write(payload,0,payload.length);
        }
    }
    int length;
    Type type;
    byte[] payload;
    public int getLength()
    {
        return length;
    }

    public void setLength(int length)
    {
        this.length = length;
    }

    public void setType(Type type)
    {
        this.type = type;
    }

    public byte[] getPayload()
    {
        return payload;
    }

    public void setPayload(byte[] payload)
    {
        this.payload = payload;
    }


}
