package cn.itcast.lucene;

import cn.itcast.lucene.dao.BookDao;
import cn.itcast.lucene.dao.impl.BookDaoImpl;
import cn.itcast.lucene.pojo.Book;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class IndexManagerTest1 {
    /**
     * 根据数据库中查询到的数据将这些数据写入到索引中
     */
    @Test
    public void createIndex() throws Exception {
        //1、创建分词器Analyzer
        Analyzer analyzer = new IKAnalyzer();
        //2、创建索引目录Directory
        Directory directory = FSDirectory.open(new File("F:itcast/test/lucene1"));
        //3、创建索引编写器配置对象IndexWriterConfig(4.10.3,analyzer)
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_4_10_3,analyzer);
        //4、创建索引编写器IndexWriter
        IndexWriter indexWriter = new IndexWriter(directory,indexWriterConfig);
        //5、创建文档，利用IndexWriter将文档写入索引目录
        //采集数据
        BookDao bookDao = new BookDaoImpl();
        List<Book> bookList = bookDao.queryBookList();
        //创建文档对象（Document）,将数据转为lucene支持的文档
        List<Document> documentList = new ArrayList<>();
        for (Book book : bookList) {
            Document document = new Document();
            document.add(new TextField("id",book.getId().toString(), Field.Store.YES));
            document.add(new TextField("bookname", book.getBookname(), Field.Store.YES ));
            document.add(new TextField("bookdesc", book.getBookdesc(), Field.Store.YES ));
            document.add(new TextField("price", book.getPrice().toString(), Field.Store.YES ));
            document.add(new TextField("pic", book.getPic(), Field.Store.YES ));
            documentList.add(document);
        }
        //使用IndexWriter对象，把文档对象写入索引库
        for (Document doc : documentList) {
            indexWriter.addDocument(doc);
        }
        //6、关闭资源
        indexWriter.close();
    }

    /**
     * 到指定的索引目录根据搜索关键字查询数据
     */
    @Test
    public void searchIndex() throws Exception{

    }




}
