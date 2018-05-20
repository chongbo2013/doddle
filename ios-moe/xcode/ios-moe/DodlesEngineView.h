#import <UIKit/UIKit.h>
#import <GLKit/GLKit.h>
#import <React/RCTComponent.h>

@class DodlesEngineView;
@protocol DodlesEngineViewDelegate <NSObject>
- (void) sendEngineEventToReact:(DodlesEngineView *)view topic:(NSString *)topic type:(NSString *) type data:(NSString *) data;
@end

@interface DodlesEngineView : UIView
@property (nonatomic, weak) id <DodlesEngineViewDelegate> delegate;
@property (nonatomic, copy) RCTDirectEventBlock onEngineEvent;
- (void) initDodlesView:(CGRect) frame;
- (void) sendEventToEngine:(NSString *)topic type:(NSString *)type data:(NSString *)data;
- (void) sendEngineEventToReact:(NSString *)topic type:(NSString *)type data:(NSString *)data;
@end
