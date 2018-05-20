#import "DodlesEngineView.h"
#import "React/UIView+React.h"

@implementation DodlesEngineView
@synthesize delegate;

- (id)initWithFrame:(CGRect)frame
{
  self = [super initWithFrame:frame];
  if (self) {
    [self initDodlesView:frame];
  }
  return self;
}

- (void)reactSetFrame:(CGRect)frame
{
  [super reactSetFrame: frame];
  [self initDodlesView:frame];
}

- (void) initDodlesView:(CGRect) frame
{
    // Overwritten in java implementation
}

- (void) sendEventToEngine:(NSString *)topic type:(NSString *)type data:(NSString *)data
{
    // Overwritten in java implementation
}

- (void) sendEngineEventToReact:(NSString *)topic type:(NSString *)type data:(NSString *)data
{
    [self.delegate sendEngineEventToReact:self topic:topic type:type data:data];
}

@end



