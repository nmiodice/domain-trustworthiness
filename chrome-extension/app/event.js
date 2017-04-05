// helper to convert a score value to an icon
function scoreToIconPath(score) {
    return "icons/" + score + ".png";
}

// helper to change the specified tab to an icon corresponding to the score
function changeIconToMatchScore(score, tabID) {
    changeIcon(scoreToIconPath(score), tabID);
}

// change the tab to the specified icon, and update the cache to reflect the new value
function changeIcon(iconPath, tabID) {
    tabIconPathStore[tabID] = iconPath;

    console.log("changing tab " + tabID + " to icon " + iconPath);
    chrome.browserAction.setIcon({
        path: iconPath,
        tabId: tabID
    });
}

// change the tab to the default value, or the one stored in the icon cache
function changeIconToCachedValueOrDefault(tabID) {
    var cachedPath = tabIconPathStore[tabID];
    if (cachedPath) {
        changeIcon(cachedPath, tabID);
    } else {
        changeIconToMatchScore(0, tabID);
    }
}

// cache for tab IDs and icon paths
var tabIconPathStore = {};


// when notified to update the icon for a tab, do it
chrome.runtime.onMessage.addListener(
    function(request, sender, sendResponse) {
        console.log("onMessage called", request, sender);
        changeIconToMatchScore(request.domainScore, sender.tab.id);
        sendResponse("icon changed to " + scoreToIconPath(request.domainScore));
    });

// reset the icon when a new URL is loaded
chrome.tabs.onUpdated.addListener(function(tabID, changeInfo) {
    console.log("onUpdated called", tabID, changeInfo);

    if (changeInfo.url) {
        // the cache entry is no longer valid, so remove it
        delete tabIconPathStore[tabID];
        changeIconToCachedValueOrDefault(tabID);
    }
});

// when the tab changes, also change the icon to match the proper tab score
chrome.tabs.onActivated.addListener(function(activeInfo) {
    console.log("onActivated called", activeInfo);
    changeIconToCachedValueOrDefault(activeInfo.tabId)
});
