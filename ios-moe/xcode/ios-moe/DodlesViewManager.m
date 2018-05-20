#import "DodlesViewManager.h"
#import "DodlesEngineView.h"
#import "RCTUIManager.h"

@implementation DodlesViewManager

RCT_EXPORT_MODULE(DodlesEngineView);


- (UIView *)view
{
    DodlesEngineView *v = [[DodlesEngineView alloc] init];
    v.delegate = self;
    return v;
}

RCT_EXPORT_VIEW_PROPERTY(onEngineEvent, RCTDirectEventBlock);

- (void) sendEngineEventToReact:(DodlesEngineView *)view topic:(NSString *) topic type:(NSString *) type data:(NSString *) data
{
    view.onEngineEvent(@{
        @"topic":topic,
        @"type":type,
        @"data":data
    });
}

RCT_EXPORT_METHOD(sendEventToEngine:(nonnull NSNumber *)reactTag
                  topic:(NSString *)topic
                  type:(NSString *)type
                  data:(NSString *)data)
{
    [self.bridge.uiManager addUIBlock:^(__unused RCTUIManager *uiManager, NSDictionary<NSNumber *, DodlesEngineView *> *viewRegistry) {
        DodlesEngineView *view = viewRegistry[reactTag];
        if (![view isKindOfClass:[DodlesEngineView class]]) {
            RCTLogError(@"Invalid view returned from registry, expecting DodlesEngineView, got: %@", view);
        } else {
            [view sendEventToEngine:topic type:type data:data];
        }
    }];
}

@end

