from bs4 import BeautifulSoup
from crawler import Crawler

class Crawler56888(Crawler):
    def __init__(self):
        Crawler.__init__(self)
        self.HOST = "http://wb.56888.net"
        self.prefix = "/OutSourceList.aspx?tendertype=4&p="

    def uniform(self, page):
        soup = BeautifulSoup(page, "html.parser")
        table = soup.table.find("table")
        for table in table.find_all("table"):
            item = {"site": "56888"}
            item["url"] = self.HOST + "/" + table.find("a").get("href")
            if item in self.data or self.exist(item):
                self.window -= 1
                continue
            s = table.find("span").text
            pos = s.find("->")
            item["from"] = s[:pos].strip()
            item["to"] = s[pos+2:].strip()
            s = table.find_all("td")[4].text    # 10-08至11-07
            pos = s.find("至")
            date = s[:pos].strip().split('-')
            deadline = s[pos+1:].strip().split('-')
            item["date"], item["deadline"] = self.lifetime(date, deadline)
            self.data.append(self.good(item))
