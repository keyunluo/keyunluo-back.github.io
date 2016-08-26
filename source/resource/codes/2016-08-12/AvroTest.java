import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.util.Utf8;
import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.File;

public class AvroTest{
    public static void main(String[] args) throws IOException {
        // 加载Schema
        Schema.Parser parser = new Schema.Parser();
        FileInputStream fin = new FileInputStream("StringPair.avsc");
        Schema schema = parser.parse(fin);
        //非静态类加载 
        //Schema schema = parser.parse(getClass().getResourceAsStream("StringPair.avsc"));

        // 新建记录
        GenericRecord datum = new GenericData.Record(schema);
        datum.put("left",new Utf8("L"));
        datum.put("right",new Utf8("R"));
        
        // 将记录序列化到输出流中
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>(schema);
        Encoder encoder = EncoderFactory.get().binaryEncoder(out, null);
        writer.write(datum, encoder);
        encoder.flush();
        out.close();
        
        // 使用反向的处理过程从字节缓冲区中读回对象
        DatumReader<GenericRecord> reader = new GenericDatumReader<GenericRecord>(schema);
        Decoder decoder = DecoderFactory.get().binaryDecoder(out.toByteArray(),null);
        GenericRecord result = reader.read(null, decoder);
        System.out.println("从字节缓冲区中读取对象");
        System.out.println("读取结果：left:"+result.get("left").toString());
        System.out.println("读取结果：right:"+result.get("right").toString());
        
        // 将Avro对象写入数据文件
        File file = new File("data.avro");
        DatumWriter<GenericRecord> writer2 = new GenericDatumWriter<GenericRecord>(schema);
        DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<GenericRecord>(writer2);
        dataFileWriter.create(schema, file);
        dataFileWriter.append(datum);
        dataFileWriter.close();
        
        // 从数据文件中读取对象
        DatumReader<GenericRecord> reader2 = new GenericDatumReader<GenericRecord>();
        DataFileReader<GenericRecord> dataFileReader = new DataFileReader<GenericRecord>(file, reader2);
        GenericRecord record = null;
        System.out.println("从数据文件中读取对象");
        while (dataFileReader.hasNext()) {
            record = dataFileReader.next(record);
 
        }
        System.out.println("读取结果：left:"+record.get("left").toString());
        System.out.println("读取结果：right:"+record.get("right").toString());
        // 关闭输入流
        fin.close();
        
        // 加载新的Schema
        Schema.Parser parsernew = new Schema.Parser();
        fin = new FileInputStream("StringPairNew.avsc");
        Schema newSchema = parsernew.parse(fin);
        
        DatumReader<GenericRecord> reader3 = new GenericDatumReader<GenericRecord>(schema, newSchema);
        Decoder decoder2 = DecoderFactory.get().binaryDecoder(out.toByteArray(),null);
        GenericRecord result2 = reader3.read(null, decoder2);
        System.out.println("新模式读取对象");
        System.out.println("读取结果：left:"+result2.get("left").toString());
        System.out.println("读取结果：right:"+result2.get("right").toString());
        System.out.println("读取结果：description:"+result2.get("description").toString());
    }
}
