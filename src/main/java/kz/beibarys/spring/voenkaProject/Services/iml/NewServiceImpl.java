package kz.beibarys.spring.voenkaProject.Services.iml;

import kz.beibarys.spring.voenkaProject.Entities.News;
import kz.beibarys.spring.voenkaProject.Entities.Users;
import kz.beibarys.spring.voenkaProject.Repositories.NewsRepository;
import kz.beibarys.spring.voenkaProject.Services.NewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NewServiceImpl implements NewService {
    @Autowired
    private NewsRepository newsRepository;

    @Override
    public void addNews(News news) {

        newsRepository.save(news);
    }
}
