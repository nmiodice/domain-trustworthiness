function getCurrentTabUrl(callback) {
    var queryInfo = {
        active: true,
        currentWindow: true
    };
    chrome.tabs.query(queryInfo, function(tabs) {
        callback(tabs[0].url);
    });
};

function displayPageRank(url) {
    var domain = new URL(url).hostname;
    if (domain.indexOf('www.') === 0) {
        domain = domain.replace('www.', '');
    }
    console.log('will try to find page rank for domain = ' + domain);
    var queryURL = "http://ec2-54-144-123-230.compute-1.amazonaws.com//pagerank/?domain=" + domain;
    console.log('query URL = ' + queryURL);

    var request = new XMLHttpRequest();
    request.responseType = 'json';
    request.open('GET', queryURL);

    request.onload = function() {
        var data = request.response;
        if (data && data.pagerank) {
            var rank = parseFloat(data.pagerank);
            console.log(rank)
            document.getElementById('status').textContent = rank
        }
    };
    request.send();
};

document.addEventListener('DOMContentLoaded', function() {
    getCurrentTabUrl(displayPageRank);
});