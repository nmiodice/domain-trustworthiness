function changeIcon(domainScore) {
    console.log('triggering change icon')
    chrome.runtime.sendMessage({
        domainScore: domainScore
    }, function(x) {
        console.log("response was: " + x)
    });
}


function rankToScore(rankInfo) {
    var log_min = Math.log10(1 + rankInfo.min_page_rank);
    var log_max = Math.log10(1 + rankInfo.max_page_rank);
    var log_rank = Math.log10(1 + rankInfo.pagerank);

    var raw_score = log_rank / (log_max - log_min);
    console.log("raw score was " + raw_score);

    if (raw_score > .5) return 3;
    if (raw_score > .3) return 2;
    if (raw_score > .2) return 1;
    if (raw_score > .1) return -1;
    if (raw_score > .01) return -2;
    return -3;
}

function requestScoreAndChangeIcon() {
    var domain = window.location.hostname;
    if (domain.indexOf('www.') === 0) {
        domain = domain.replace('www.', '');
    }

    var protocol = location.protocol;
    var scoreServer = "ec2-54-144-123-230.compute-1.amazonaws.com";

    jQuery.getJSON(protocol + "//" + scoreServer + "/pagerank/?domain=" + domain,
        function(response) {
            console.log('page rank response found', response);
            changeIcon(rankToScore(response));
        }
    );
}

requestScoreAndChangeIcon();
