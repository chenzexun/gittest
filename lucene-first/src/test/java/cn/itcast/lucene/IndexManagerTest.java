package cn.itcast.lucene;

import cn.itcast.lucene.dao.BookDao;
import cn.itcast.lucene.dao.impl.BookDaoImpl;
import cn.itcast.lucene.pojo.Book;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class IndexManagerTest {
    /**
     * 根据数据库中查询到的数据将这些数据写入到索引中
     */
    @Test
    public void createIndex() throws Exception {
        //1.采集数据
        BookDao bookDao = new BookDaoImpl();
        List<Book> bookList = bookDao.queryBookList();
        //2.创建文档对象（Document）,将数据转为lucene支持的文档
        List<Document> documentList = new ArrayList<>();
        Document document = null;
        for (Book book : bookList) {
            document = new Document();
            document.add(new TextField("id", book.getId().toString(), Field.Store.YES));
            document.add(new TextField("bookname", book.getBookname(), Field.Store.YES ));
            document.add(new TextField("bookdesc", book.getBookdesc(), Field.Store.YES ));
            document.add(new TextField("pic", book.getPic(), Field.Store.YES ));
            document.add(new TextField("price", book.getPrice().toString(), Field.Store.YES ));

            documentList.add(document);
        }
        // 3.创建分析器对象（Analyzer），用于分词
        Analyzer analyzer = new IKAnalyzer();
        //4.创建索引库的配置对象（IndexWriterConfig），配置索引库
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_4_10_3,analyzer);
        //5.创建索引库的目录对象（Directory），指定索引库的存储位置
        Directory directory = FSDirectory.open(new File("F:itcast/test/lucene"));
        //6.创建索引库操作对象（IndexWriter），操作索引库
        IndexWriter indexWriter = new IndexWriter(directory,indexWriterConfig);
        //7.使用IndexWriter对象，把文档对象写入索引库
        for (Document doc : documentList) {
            indexWriter.addDocument(doc);
        }
        //8.释放资源
        indexWriter.close();
    }

    /**
     * 到指定的索引目录根据搜索关键字查询数据
     */
    @Test
    public void searchIndex() throws Exception{
        //索引存放目录
       Directory directory = FSDirectory.open(new File("F:itcast/test/lucene"));
        //创建索引读入对象
        IndexReader indexReader = DirectoryReader.open(directory);
        //创建索引搜索器
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        //创建标准分词器
        Analyzer analyzer = new IKAnalyzer();
        //创建查询分析器；参数1：域名，参数2：分词器
        QueryParser queryParser = new QueryParser("bookname",analyzer);
        //创建查询对象
        Query query = queryParser.parse("java");
        //搜索；符合查询条件的前10条；参数1：查询对象，参数2：查询的返回最大数
        TopDocs topDocs = indexSearcher.search(query, 10);
        System.out.println("符合本次查询的总文档数（命中数）为：" + topDocs.totalHits);
        //获取得分文档id集合
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (int i = 0; i < scoreDocs.length; i++) {
            ScoreDoc scoreDoc = scoreDocs[i];
            System.out.println("-----------------------------");
            System.out.println("文档在lucene中的id为：" + scoreDoc.doc + "；得分为：" + scoreDoc.score);
            //根据文档id查询对应的文档并输出
            Document document = indexSearcher.doc(scoreDoc.doc);
            System.out.println("id = " + document.get("id"));
            System.out.println("bookname = " + document.get("bookname"));
            System.out.println("pic = " + document.get("pic"));
            System.out.println("price = " + document.get("price"));
            System.out.println("bookdesc = " + document.get("bookdesc"));
        }

        //关闭资源
        indexReader.close();

    }




}
